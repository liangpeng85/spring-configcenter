1. configcenter zk struct:
configcenter
           |--groupName
                       |---version
                                  |
                                  |----consumers
                                               |----ip$uuid
                                               |----ip$uuid
                                  
                                  |----configData
                                               |----db.properties
                                               |----app.properties

example:
configcenter
           |
           |-----DataBaseService
                               |----0.1
                                      |----consumers
                                                   |----192.168.1.12$8c1f4530-309c-4931-bcf4-ccbd48ad324d
                                                   |----192.168.2.12$a056f35e-0ac4-4c41-b1c3-6f889197eb1c
                                      |----configData
                                                   |----db.properties
                                                   |----app.properties
                               |----0.2
                                      |----consumers
                                                   |----192.168.1.13$c67c554d-b4b4-4e03-abca-7802647a1978
                                                   |----192.168.2.13$a6371b8e-cb7e-4158-80a6-d8f9cb915ed7
                                      |----configData
                                                   |----db.properties
                                                   |----app.properties
                           
2. application spring config
   2.1 import namespace:xmlns:config="http://www.springframework.org/schema/configcenter"
   2.2 add schema location:
   	http://www.springframework.org/schema/configcenter
	http://www.springframework.org/schema/configcenter/spring-configcenter-0.0.1.xsd
   2.3 configuation
      <config:annotation />
	  <config:config zkServer="localhost:3421" version="0.1" group="AppGroup" charset="utf-8" />
   2.4 program usage:
      add @Config annotation on field or method 
      
      @Config(name = "jdbc.url")
       private String jdbcUrl;
       
       or
       
       @Config(name="jdbc.url")
       public void setJdbcUrl(String jdbcUrl){this.jdbcUrl = jdbcUrl;}