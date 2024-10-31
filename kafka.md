## Kafka Processo Manual: 
1. Inicializar container kafka: 
  docker run -d -p 9092:9092 --name broker apache/kafka:latest
2. Acessar console do kafka:
   docker exec --workdir /opt/kafka/bin/ -it broker sh
3. Produzir uma mensagem manualmente
./kafka-console-producer.sh --bootstrap-server localhost:9092 --topic insurance-policy-created
 {    "id": 1,    "insurance_policy_id": 756969,    "created_at": "2024-10-27T23:11:48.312894" }
 
Docker: Reconstruir container da aplicação
> docker build -t insurance-quote .