package com.myorg;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.rds.*;
import software.constructs.Construct;

import java.util.Collections;

public class RdsStack extends Stack {
    // Construtor que aceita scope, id e vpc, delegando para o outro construtor com props como null
    public RdsStack(final Construct scope, final String id, Vpc vpc) {
        this(scope, id, null, vpc);
    }

    // Construtor principal que inicializa a stack com scope, id, props e vpc
    public RdsStack(final Construct scope, final String id, final StackProps props, Vpc vpc) {
        // Chama o construtor da classe pai (Stack) para inicializar a stack
        super(scope, id, props);

        // Cria um parâmetro CloudFormation para a senha do banco de dados RDS
        CfnParameter databasePassword = CfnParameter.Builder.create(this, "databasePassword")
                .type("String")
                .description("The RDS instance password")
                .build();

        // Obtém o grupo de segurança padrão da VPC e adiciona uma regra de saída para o porto 3306 (MySQL)
        ISecurityGroup securityGroup = SecurityGroup.fromSecurityGroupId(this, "DefaultSecurityGroup", vpc.getVpcDefaultSecurityGroup());
        securityGroup.addEgressRule(Peer.anyIpv4(), Port.tcp(3306));

        // Cria uma instância de banco de dados RDS com as configurações especificadas
        DatabaseInstance databaseInstance = DatabaseInstance.Builder
                .create(this, "Rds01")
                .instanceIdentifier("aws-project01-db") // Identificador da instância
                .engine(DatabaseInstanceEngine.mysql(
                        MySqlInstanceEngineProps.builder()
                                .version(MysqlEngineVersion.VER_5_7) // Versão do MySQL
                                .build()))
                .vpc(vpc) // VPC onde o RDS será criado
                .credentials(Credentials.fromUsername("admin",
                        CredentialsFromUsernameOptions.builder()
                                .password(SecretValue.plainText(databasePassword.getValueAsString())) // Senha do banco de dados
                                .build()))
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO)) // Tipo de instância
                .multiAz(false) // Não usa Multi-AZ
                .allocatedStorage(10) // Armazenamento alocado em GB
                .securityGroups(Collections.singletonList(securityGroup)) // Grupo de segurança associado
                .vpcSubnets(SubnetSelection.builder()
                        .subnets(vpc.getPrivateSubnets()) // Sub-redes privadas da VPC
                        .build())
                .build();

        // Cria uma saída CloudFormation para o endpoint do banco de dados RDS
        CfnOutput.Builder.create(this, "rds-enpoint")
                .exportName("rds-enpoint")
                .value(databaseInstance.getDbInstanceEndpointAddress())
                .build();

        // Cria uma saída CloudFormation para a senha do banco de dados RDS
        CfnOutput.Builder.create(this, "rds-password")
                .exportName("rds-password")
                .value(databasePassword.getValueAsString())
                .build();
    }
}
