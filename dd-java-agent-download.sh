#!/bin/bash

set -o errexit

datadog_agent_version="1.35.2"

curl -fL --no-progress-meter -o dd-java-agent.jar \
    https://search.maven.org/remotecontent?filepath=com/datadoghq/dd-java-agent/${datadog_agent_version}/dd-java-agent-${datadog_agent_version}.jar
curl -fL --no-progress-meter -o dd-java-agent.jar.sha256 \
    https://search.maven.org/remotecontent?filepath=com/datadoghq/dd-java-agent/${datadog_agent_version}/dd-java-agent-${datadog_agent_version}.jar.sha256
echo "$(cat dd-java-agent.jar.sha256)  dd-java-agent.jar" | sha256sum -c
rm dd-java-agent.jar.sha256
mv dd-java-agent.jar /opt/dd-java-agent.jar
chmod 755 /opt/dd-java-agent.jar
