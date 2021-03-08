package se.alipsa.pd

import se.alipsa.pd.model.Environment
import se.alipsa.pd.model.PasswordSource
import se.alipsa.pd.util.IOUtil
import se.alipsa.pd.util.PasswordPrompt

import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller

class ProjectDeployer {

    DeploymentConfig config
    Closure setupActions
    LinkedHashMap<String, Closure> actionMap = new LinkedHashMap<>()

    static ProjectDeployer create(String configFile){
        JAXBContext context = JAXBContext.newInstance(DeploymentConfig.class)
        DeploymentConfig config = (DeploymentConfig) context.createUnmarshaller()
                .unmarshal(new File(configFile))
        handleSshPassword(config)
        return new ProjectDeployer(config)
    }

    ProjectDeployer(DeploymentConfig config) {
        this.config = config
    }

    def createSetupActions(Closure actions) {
        setupActions = actions
    }

    def createActions(String targetName, Closure actions) {
        actionMap.put(targetName, actions)
    }


    File fetchJarFromRepository(String baseUrl, String groupArtifactVersion) {
        if (baseUrl == null || !baseUrl.contains("://")) {
            throw new InputException("Wrong baseUrl format for ${baseUrl}, not a valid url")
        }
        if (groupArtifactVersion == null || groupArtifactVersion.length() < 6) {
            throw new InputException("Wrong groupArtifactVersion format: ${groupArtifactVersion}, expected groupName:artifactName:version")
        }
        def gav = groupArtifactVersion.split(":")
        if (gav.length != 3) {
            throw new InputException("Wrong groupArtifactVersion format: ${groupArtifactVersion}, expected groupName:artifactName:version")
        }
        def group = gav[0].replace('.', '/')
        def artifact = gav[1]
        def version = gav[2]
        baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl
        URL url = new URL("${baseUrl}/${group}/${artifact}/${version}/${artifact}-${version}.jar")
        def tmpFile = File.createTempFile(artifact, ".jar")
        IOUtil.download(url, tmpFile)
        return tmpFile
    }

    void download(URL url, File toFile) {
        IOUtil.download(url, toFile)
    }

    // TODO: it is perhaps smarter to execute one host for each target
    //  and then proceed to the second host for each target and so forth
    //  the current way targets all hosts in one target and then moves on to the next target
    def deploy(String environmentName) {
        Environment env = config.environmentForName(environmentName)
        setupActions.forEach(a -> a.call())
        env.getTargets().forEach(t -> {
            Closure c = actionMap.get(t.name)
            t.getHosts().forEach(host -> {
                Deployment deployment = new Deployment(config.global.sshUser, config.global.sshPassword, host)
                c.call(deployment)
            })
        })
    }

    static void handleSshPassword(DeploymentConfig deploymentConfig) {
        if (deploymentConfig.global.sshPasswordSource == PasswordSource.prompt) {
            deploymentConfig.global.sshPassword = PasswordPrompt
                    .readPassword("Enter ssh password for ${deploymentConfig.global.sshUser}: ")
        } else {
            throw new IllegalArgumentException("source ${deploymentConfig.global.sshPasswordSource} is not supported")
        }
    }

    void handleSshPassword() {
        handleSshPassword(config)
    }

    String configAsXml() {
        JAXBContext context = JAXBContext.newInstance(DeploymentConfig.class)
        Marshaller mar= context.createMarshaller()
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE)
        StringWriter sw = new StringWriter()
        mar.marshal(config, sw)
        return sw.toString()
    }
}
