# I. Pages (TODO): #
### 1. Homepage: ###
### 2. Login: ###
### 3. Forgot Password: ###
### 4. User Information: ###
### 5. Dashboards: ###
- Manage Suppliers: GET, POST, PUT, DELETE
- Manage Warehouses: GET, POST, PUT, DELETE
- Manage All Goods (Warehouse_Goods): GET, POST, PUT, DELETE
- Manage Bill Of Receiving (BillOfReceiving_Goods): GET, POST, DELETE
- Manage Bill Of Dispatching (BillOfDispatching_Goods): GET, POST, DELETE
# II. Installation: #
### 1. Services:
> https://www.docker.com/products/docker-desktop/

> https://www.jetbrains.com/idea/download/?section=windows
> https://www.oracle.com/java/technologies/downloads/#jdk21-windows

> https://code.visualstudio.com/

> https://dev.mysql.com/downloads/workbench/

### 2. Setup: ###
a. Setup IntelliJ + JDK21 step by step:
> https://www.youtube.com/watch?v=-hxCPXjYWJU

b. Install Docker step by step:
- Sign in Docker account.
- Open Docker > Terminal (Bottom Right).
> docker run --name mysql_8_debian -p 3306:3306 -e MYSQL_ROOT_PASSWORD=sa -d mysql:8.0-debian

> docker run --name redis_latest_11_19_2024 -p 6379:6379 -d redis
- Connect with MySQL "localhost:3306(root:sa)" by MySQL Workbench to test the installation result.

c. Turn on Mobile hotspot (or using Ngrok) to public Spring APIs for ReactJS to code.