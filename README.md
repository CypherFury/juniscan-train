# Juniscan

**Juniscan** is a powerful and user-friendly explorer designed specifically for **June (Ğ1)**, a cryptocurrency based on the Duniter framework. With a seamless integration using WebSocket RPC, Juniscan provides real-time access to data such as blocks, transactions, and justifications from the June blockchain.

## 🚀 Features

- **Real-Time Blockchain Data**: Fetch and display the latest blocks and their details using WebSocket communication.
- **Extensible Design**: Built with Spring Boot for easy scalability and customization.
- **Error Handling and Logging**: Robust error handling and structured logging using SLF4J.
- **Lightweight and Efficient**: Designed to provide fast and accurate insights into the June blockchain.

---

## 🐳 Setting Up Docker and Kafka

Follow these steps to set up Docker, run Kafka, and create a Kafka consumer group named `chain-group`.

### Step 1: Install Docker

1. **Download Docker**:
   - Visit the [Docker website](https://www.docker.com/products/docker-desktop/) and download Docker Desktop for your operating system.

2. **Install Docker**:
   - Follow the installation instructions for your OS.
   - Ensure Docker is running by typing the following command in your terminal:
     ```bash
     docker --version
     ```

3. **Start Docker**:
   - Open Docker Desktop and ensure the Docker engine is running.

---

### Step 2: Run Kafka with Docker

Kafka requires both a **Zookeeper** and a **Kafka** broker to run. We'll use Docker images to set them up.

1. **Create a Docker Network**:
   - Kafka and Zookeeper need to communicate within the same network. Create a custom Docker network:
     ```bash
     docker network create kafka-net
     ```

2. **Run Zookeeper**:
   - Start a Zookeeper container:
     ```bash
     docker run -d --name zookeeper --network kafka-net -e ALLOW_ANONYMOUS_LOGIN=yes bitnami/zookeeper:latest
     ```

3. **Run Kafka**:
   - Start a Kafka broker container:
     ```bash
     docker run -d --name kafka --network kafka-net -e KAFKA_CFG_ZOOKEEPER_CONNECT=zookeeper:2181 -e ALLOW_PLAINTEXT_LISTENER=yes -e KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 bitnami/kafka:latest
     ```

4. **Verify Kafka is Running**:
   - Check if Kafka and Zookeeper are running:
     ```bash
     docker ps
     ```

   - You should see both containers (`kafka` and `zookeeper`) listed.

---

### Step 3: Create a Kafka Consumer Group

Kafka uses consumer groups to manage message consumption. Let’s create a group named `chain-group`.

1. **Access the Kafka Container**:
   - Open a terminal in the Kafka container:
     ```bash
     docker exec -it kafka /bin/bash
     ```

2. **Create the Consumer Group**:
   - Use the Kafka CLI tools to create the `chain-group`:
     ```bash
     kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic your-topic-name --group chain-group
     ```

   - Replace `your-topic-name` with the topic name your application will use.

3. **Verify the Consumer Group**:
   - Exit the Kafka container and list all consumer groups:
     ```bash
     docker exec -it kafka kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list
     ```

   - You should see `chain-group` in the list.

---

## 🛠 Setting Up MariaDB

### Step 1: Install MariaDB Server

1. **Install MariaDB**:
   - Run the following command to install MariaDB:
     ```bash
     sudo apt update
     sudo apt install mariadb-server
     ```

2. **Start and Enable MariaDB**:
   - Ensure MariaDB is running and starts automatically on boot:
     ```bash
     sudo systemctl start mariadb
     sudo systemctl enable mariadb
     ```

3. **Secure the Installation**:
   - Run the secure installation script to set a root password and disable unsafe defaults:
     ```bash
     sudo mysql_secure_installation
     ```

---

### Step 2: Create the Database and User

1. **Access the MariaDB Shell**:
   - Open the MariaDB shell:
     ```bash
     sudo mysql -u root -p
     ```

2. **Create the Database**:
   - Create a database named `juniscan`:
     ```sql
     CREATE DATABASE juniscan;
     ```

3. **Create a User**:
   - Create a user `username` with password `password` (replace these with your own credentials for production):
     ```sql
     CREATE USER 'username'@'localhost' IDENTIFIED BY 'password';
     ```

4. **Grant Permissions**:
   - Grant all privileges on the `juniscan` database to the user:
     ```sql
     GRANT ALL PRIVILEGES ON juniscan.* TO 'username'@'localhost';
     FLUSH PRIVILEGES;
     ```

5. **Exit the Shell**:
   - Exit MariaDB:
     ```sql
     EXIT;
     ```

6. **Test the Connection**:
   - Verify that the user can access the database:
     ```bash
     mysql -u username -p
     ```
   - Enter the password `password`, then:
     ```sql
     SHOW DATABASES;
     ```

   - You should see the `juniscan` database listed.

---

## 📂 Next Steps

- **Configure Juniscan**:
   - Update the `application.properties` or `application.yml` file in your project with the MariaDB and kafka details:
     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3306/juniscan
     spring.datasource.username=username
     spring.datasource.password=password
     spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
     
     spring.kafka.bootstrap-servers=localhost:9092
     spring.kafka.consumer.group-id=chain-group
     ```

- **Start Juniscan**:
   - Build and run the Juniscan application:
     ```bash
     ./mvnw spring-boot:run
     ```

- **Explore Blockchain Data**:
   - Open the application in your browser or API client and start exploring!

---

## 📚 Resources

- [Docker Documentation](https://docs.docker.com/)
- [Kafka Documentation](https://kafka.apache.org/documentation/)
- [Spring Kafka Reference Guide](https://docs.spring.io/spring-kafka/docs/current/reference/html/)
- [MariaDB Setup Guide](https://www.digitalocean.com/community/tutorials/how-to-install-mariadb-on-ubuntu-20-04)

---

Happy exploring with **Juniscan**!
