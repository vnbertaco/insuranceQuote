# Desafio Software Engineer - Seguradora

 **Descrição**:   API REST para processamento de requisições de cotação de seguro. 
 
    - Framework Springboot 3.3.5
    - Java OpenJDK 17.0
    - IDE IntelliJ IDEA  
    - Mensageria Kafka
    - Banco em memória H2
    - Docker
    - MockServer
    
 

## Execução
Para executar o container da aplicação e o broker kafka utilizando docker compose, abra o console, navegue ate a pasta da aplicação e digite :
>docker compose up

Para finalizar utilize ctrl+c


## Broker e Tópicos Kafka

Os seguintes topicos são criados durante a execução pela aplicação:

>insurance-quote-received
> 
>insurance-policy-created
 

Para simular uma resposta do serviço(broker) de politicas:
1. Acessar console do kafka:
 >   docker exec --workdir /opt/kafka/bin/ -it broker sh
2. Produzir uma mensagem   
 >  ./kafka-console-producer.sh --bootstrap-server localhost:9092 --topic insurance-policy-created

Exemplo de Resposta:
>   {    "id": 1,    "insurance_policy_id": 756969,    "created_at": "2024-10-27T23:11:48.312894" }
 
## Console do Banco de dados em memória H2
Funciona executando a aplicação no console: 
> mvn spring-boot:run

> localhost:8080/h2-console/ 

 User: sa
 Password: sa
  
## Requisições REST 

 [Collection Insomina](./quoted-insurance.json)

### GET - Obter uma quotação salva pelo ID
> curl --request GET \
--url http://localhost:8080/insurance/quote/1 \
--header 'User-Agent: insomnia/9.2.0'
>

### POST - Requisitar uma cotação
 >curl --request POST \
 --url http://localhost:8080/insurance/quote \
 --header 'Content-Type: application/json' \
 --header 'User-Agent: insomnia/9.2.0' \
 --data '{
 "product_id": "1b2da7cc-b367-4196-8a78-9cfeec21f587",
 "offer_id": "adc56d77-348c-4bf0-908f-22d402ee715c",
 "category": "HOME",
 "total_monthly_premium_amount": 50.25,
 "total_coverage_amount": 25000.00,
 "coverages": {
 "Incêndio": 250000.00,
 "Desastres naturais": 500000.00,
 "Responsabiliadade civil": 75000.00
 },
 "assistances": [
 "Encanador",
 "Eletricista",
 "Chaveiro 24h"
 ],
 "customer": {			
 "document_number": "36205578900",
 "name": "John Wick",
 "type": "NATURAL",
 "gender": "MALE",
 "date_of_birth": "1973-05-02",
 "email": "johnwick@gmail.com",
 "phone_number": 11950503030
 }
 }'
 > 

### Relatório de cobertura de testes unitários
 
[Relatório Jacoco](./target/site/jacoco/index.html)
