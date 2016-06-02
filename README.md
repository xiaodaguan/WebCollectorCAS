# WebCollectorDemo



_crawlers based on webcollector_  
若干个自己基于webcollector实现的爬虫，包括百度新闻搜索、微博搜索(使用selenium 模拟登录微博)、京东商品基本信息采集等  

## quick start
-  编写一个爬虫
  1.  在data/定义数据结构
  2.  在DataPersistence实现数据数据持久化方法
  3.  编写crawler
    -  编写配置
    -  实现visit()方法

> 详见main.java.JdSearchCrawler，一个爬取京东商品基本信息的简单爬虫,列表页+详情页模式采集  


## 目录  
src/main/java/  
&nbsp;&nbsp;&nbsp;&nbsp;|_data/  --定义数据类型  
&nbsp;&nbsp;&nbsp;&nbsp;|_db/  --数据库操作  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|_DataPersistence  --数据库操作  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|_ORM  --对象关系映射  
&nbsp;&nbsp;&nbsp;&nbsp;|_util/ --工具类  
&nbsp;&nbsp;&nbsp;&nbsp;|_crawler/ --各种crawler

...
