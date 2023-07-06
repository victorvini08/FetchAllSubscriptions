# FetchAllSubscriptions
Oracle FAW Project to fetch all subscriptions and sizing for tenants from Oracle cloud and run it periodically using a scheduler.

To run, use the following commands: 

1. Connect to ssh via: \
ssh -i s2s-inst-test -L 8000:169.254.169.254:80 opc@144.25.95.177 \
   ( Note: s2s-inst-test file should be present in the directory)
2. Maven clean install: \
mvn clean install -T 6
3. Run using java: \
java --add-exports java.base/sun.security.util=ALL-UNNAMED --add-exports java.base/sun.security.x509=ALL-UNNAMED --add-exports=java.base/sun.security.internal.spec=ALL-UNNAMED --add-opens=java.base/jdk.internal.misc=ALL-UNNAMED --add-opens=java.base/java.lang.invoke=ALL-UNNAMED -jar target/TenantSubscriptionReport-2.1-SNAPSHOT.jar

Note: In FAWSizingUtils, I am loading the sizing config stored from my local computer as I did not have access to read sizing config from remote bucket. Pleasee change path of the file as needed.