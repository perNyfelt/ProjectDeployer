# ProjectDeployer
The goal of ProjectDeployer is to be a project deployment framework for programmers.

It differs from tools like ansible and chef in the sense that instead of describing
a desired state through configuration, ProjectDeployer feels more like writing a script
of deployment stuff to do.

Configuration is still needed but in ProjectDeployer, configuration is exactly what it
sounds like and not "configuration programming" i.e. creating tasks and actions
through complex configuration. This way you end up with a simple to understand
configuration, and a groovy script (or java program) that describes what should be done which greatly
improves readability and debugging ability.

Assuming the following config file:
```xml
<deploymentConfig>
    <!-- Deployment example for an application called glow -->
    <global>
        <sshUser>pernyf</sshUser>
        <!-- source can be sshpass, prompt, cert, keyring -->
        <sshPassword source="prompt"/>
    </global>
    <environment name="test">
        <target name="backend">
            <host>behost1.test.alipsa.se:22022</host>
        </target>
        <target name="frontend">
            <host>fehost1.test.alipsa.se:22022</host>
        </target>
    </environment>
    <environment name="prod">
        <target name="backend">
            <host>behost1.prod.alipsa.se:22022</host>
            <host>behost2.prod.alipsa.se:22022</host>
        </target>
        <target name="frontend">
            <host>fehost1.prod.alipsa.se:22022</host>
            <host>fehost2.prod.alipsa.se:22022</host>
        </target>
    </environment>
</deploymentConfig>
```
...The following deploy script (named deployGlow.groovy) can be written to deploy to either test or prod environments
```groovy
@Grab('se.alipsa:ProjectDeployer:1.0.0')
import se.alipsa.pd.*

String serverUser = "glow"
String alipsaNexus ="http://localhost:8080/nexus"
String backendServiceName = "glow-backend.service" 
String frontEndServiceName = "glow-frontend.service"
String javaVersion="11.0.10"
String baseTargetDir="/usr/local/glow"

File glowBackendJar
File glowFrontEndZip

def pd = ProjectDeployer.create("myDeploymentConfig.xml")
pd.addSetupActions { 
    glowBackendJar = pd.fetchFromRepository(alipsaNexus, "se.alipsa:glow-backend:1.2")
    glowFrontEndZip = pd.fetchFromRepository(alipsaNexus, "se.alipsa:glow-frontend:1.2")
}

// d is an instance of Deployment (there is one Deployment for each host)
pd.createActions "backend", { d ->
    {
        d.createServerUser(serverUser)
        d.stopService(backendServiceName)
        String javaHome = d.installJava(javaVersion, baseTargetDir)
        d.copy( fromFile: glowBackendJar, 
                toFile: "${baseTargetDir}/backend/${glowBackendJar.getName()}", 
                owner: serverUser, 
                permissions: "g=rwx,u=rwx,o=r")
        d.createService(
                name: backendServiceName, 
                exec: "${javaHome}/bin/java -jar ${glowBackendJar.getName()}", 
                runas: serverUser)
        d.startService(backendServiceName)
    }
}

pd.createActions "frontend", { d ->
    {
        d.createServerUser(serverUser)
        d.stopService(frontEndServiceName)
        String nginxVersion = d.ssh("sudo apt update && sudo apt install nginx && sudo nginx -v")
        println("nginx version is now ${nginxVersion}")
        String zipPath = "${baseTargetDir}/frontend/${glowFrontEndZip.getName()}"
        d.copy(glowFrontEndZip, zipPath, "g=rw,u=rw,o=r")
        d.ssh("unzip ${zipPath}")
        d.createService(name: frontEndServiceName,
                content: """[Unit]
                Description=The NGINX HTTP server
                After=syslog.target network-online.target remote-fs.target nss-lookup.target
                Wants=network-online.target
                        
                [Service]
                Type=forking
                PIDFile=/run/nginx.pid
                ExecStartPre=/usr/sbin/nginx -t
                ExecStart=/usr/sbin/nginx
                ExecReload=/usr/sbin/nginx -s reload
                ExecStop=/bin/kill -s QUIT $MAINPID
                PrivateTmp=true

                [Install]
                WantedBy=multi-user.target"""
                )
        d.startService(frontEndServiceName)
    }
}
// relevant actions will now run on each host
// this.args[0] is the first argument passed to this script
// it will be validated against existing environments
pd.deploy args[0]
```
You can then deploy to test as follows:
`groovy deployGlow.groovy test`

