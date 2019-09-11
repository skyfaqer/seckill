# Seckill

### A solution of high concurrency in online-shopping web application

In China, large online-shopping websites (e.g. www.taobao.com, www.jd.com) hold events of sale promotion every year in which many products are sold at very low prices. This kind of event is called **Seckill** (Second kill, 秒杀).

Seckill events are featured with high concurrency of user requests in a very short period of time, because such events always start at a given time, and the amounts of users of such websites in China are extremely large while the stocks of products are limited. Solutions that deal with such extremely high concurrency are required.

### How this project tries to address the above issue

First of all, this project simulates a seckill event by building a full-stack web application selling mobile phones at a very low price.

The back end of this application is built with SpringBoot, using Mybatis for database management. It also enables distributed session on multiple servers by storing user token in cookies and a Redis database, which is much more efficient than MySQL.

The front end of this application is simply based on Bootstrap and Thymeleaf, in order to focus on the business logic.

To ensure the correctness of the business logic, I used the RabbitMQ message queue. It makes sure that users' requests of purchases are forwarded to a message queue first, then processed one by one. Therefore, the stock of goods will not be decreased to a negative number, and the appearance of redundant orders becomes impossible.

To optimize the performance under high concurrency, I cached the goods list page and the frequently accessed goods info in Redis database, and made the goods detail and order detail pages static so that they can be cached at browser end. Using Redis also allows the stock of goods to be decreased beforehand in Redis database, thus reducing the accesses of MySQL database.

Moreover, to deal with possible malicious accesses, I used 2 approaches: hiding the real URL of seckill page; limiting user's count of accesses within a certain period of time. To make it safer, captcha will also be shown when a user clicks the "buy" button, which is intended for verifying the legal access of seckill API.

I used JMeter to do load test on this application and measure its performance (based on QPS). The comparison of results (before and after optimization) are shown below:

Seckill API (accessed when user clicks "buy", presumably most frequently accessed):

|Concurrency |1000 * 10        |5000 * 10          |
|------------|-----------------|-----------------  |
|QPS (before)|1669 / sec       |1426 / sec         |
|QPS (after) |3659 ~ 4529 / sec|2685 ~ 3450 / sec  |

Goods list page:

|Concurrency |1000 * 10        |5000 * 10          |
|------------|-----------------|-----------------  |
|QPS (before)|2140 / sec       |1323 / sec         |
|QPS (after) |3368 ~ 4210 / sec|2970 ~ 4334 / sec  |

