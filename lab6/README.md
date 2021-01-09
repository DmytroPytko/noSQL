# Lab 6
# Prerequisites

- Акаунт на azure
- Акаунт на google cloud

# GCP Preparation

 Після створення billing account - стає доступним увесь функціонал.
 
 1. Створюємо віртуальний образ у сервісі `Compute Engine` з дефолтними налаштуваннями.
 2. Після деплою віртуальної машини необхідно налаштувати правила фаєрволу, аби пізніше мати змогу доступатись до нашого
   інстансу по зовнішній IP-адресі, а також для коректного читання Kiban'ою каталогів ElasticSearch'а.
   
  2a. Налаштовуємо рейндж IP-адрес для TCP-портів 9200(ElasticSearch) та 5601(Kibana).
   
 3. Тепер для встановлення необхідного `ELK` стеку логінимось напряму до машини за допомогою `ssh-з'єднання` і виконуємо
   через `su/sudo` наступні команди.
 3. Тепер для встановлення необхідного `ELK` стеку логінимось напряму до машини за допомогою `ssh-з'єднання` і виконуємо
   через `su/sudo` наступні команди.
   
   3a. Встановлення Java

     ```sh
     $ sudo apt-get install default-jre
     ```
   
   3b. Встановлення ElasticSearch
     ```sh
     $ wget -qO - https://packages.elastic.co/GPG-KEY-elasticsearch | sudo apt-key add -
     $ sudo apt-get install elasticsearch
     $ sudo vi /etc/elasticsearch/elasticsearch.yml 
     # розкоментовуємо рядок network.host і сетаємо для нього значення “0.0.0.0” 
     $ sudo service elasticsearch restart
     ```
   
   3c. Встановлення Logstash
     ```sh
     $ sudo apt-get install apt-transport-https
     $ echo "deb https://artifacts.elastic.co/packages/5.x/apt stable main" | sudo tee -a /etc/apt/sources.list.d/elastic-5.x.list
     $ sudo apt-get update
     $ sudo apt-get install logstash
     $ sudo service logstash start
     ```
   
   3d. Встановлення Kibana
     ```sh
     $ echo "deb http://packages.elastic.co/kibana/5.3/debian stable main" | sudo tee -a /etc/apt/sources.list.d/kibana-5.3.x.list
     $ sudo apt-get update
     $ sudo apt-get install kibana
     $ sudo vi /etc/kibana/kibana.yml
     # розкоментовуємо та вказуємо server.port: 5601 та server.host: “0.0.0.0”
     $ sudo service kibana start
     ```

4. Для перевірки коректного запуску ElasticSearch використовуємо наступну команду:
     ```sh
     $ sudo journalctl -u elasticsearch
     ```

   Найбільш поширений експешн пов'язаний із відсутністю фізичної пам'яті, оскільки віртуальна машина швидко заповнює все
   вільне місце - для цього
   варто [очистити тимчасові каталоги](https://www.omgubuntu.co.uk/2016/08/5-ways-free-up-space-on-ubuntu) та зробити
   перезапуск сервісу ElasticSearch. (`$ sudo service elasticsearch restart`)
   
5. Після усіх кроків перейдіть по `http://<ephemeral-ip>:5601` та `http://<ephemeral-ip>:9200`, аби перевірити чи
   сервіси є в робочому стані.

# Azure Preparation

1. Створюємо нову resource group або ж використовуємо попередню, створену під час виконання 5 лабораторної роботи.
2. Створюємо Logic App.
   
   2a. Створюємо початковий тригер для Event Hub, вказавши інтервал перевірки отримання нових даних та максимальну
   кількість об'єктів.

   

   2b. Створюємо також послідовний крок для надсилання HTTP-запиту після отримання даних з відповідним типом запиту та
   вказаною адресою.

   

# Execution

Для відображення даних в ElasticSearch та їх візуалізації у Kibana, необхідно затригерити нашу Logic App, завантаживши
нові дані у вказаний Event Hub, таким чином виконається логіка:
`Дані закинуто до Event Hub -> Body кожного івенту, отриманого Event Hub'ом надіслано у вигляді POST запиту до 9200 порту нашої VM -> ElasticSearch створює новий каталог або додає дані у вже існуючий -> Kibana індексує каталог ElasticSearch'a і робить дані доступними для візуалізації`
. Тригер у Logic App також можна заранити та перевірити, очікуваний результат повинен бути наступним:


Врешті-решт, дані повинні з'явитись у Index Pattern'і в Kibana:

   
   
   
   
   
   
