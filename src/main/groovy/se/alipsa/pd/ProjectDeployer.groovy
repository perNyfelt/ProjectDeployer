package se.alipsa.pd

import se.alipsa.pd.model.Environment
import se.alipsa.pd.model.PasswordSource
import se.alipsa.pd.util.PasswordPrompt

import javax.xml.bind.JAXBContext

class ProjectDeployer {

    DeploymentConfig config
    List<Closure> setupActions = new ArrayList<>()
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

    def addSetupActions(Closure actions) {
        setupActions.add(actions)
    }

    def createActions(String targetName, Closure actions) {
        actionMap.put(targetName, actions);
    }

    def File fetchFromRepository(String baseUrl, String artifact) {
        return null
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
                    .readPassword("Enter ssh password for ${deploymentConfig.global.sshUser}")
        } else {
            throw new IllegalArgumentException("source ${deploymentConfig.global.sshPasswordSource} is not supported")
        }
    }
}
