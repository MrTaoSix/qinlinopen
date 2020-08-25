# 该镜像需要依赖的基础镜像
FROM java:8

VOLUME /tmp
# 将当前目录下的jar包复制到docker容器的/目录下
ADD qinlinopen-0.0.1-SNAPSHOT.jar qinlinopen-docker-file.jar
# 运行过程中创建一个mall-tiny-docker-file.jar文件
RUN bash -c 'touch /qinlinopen-docker-file.jar'
# 声明服务运行在1818端口
EXPOSE 1818
# 指定docker容器启动时运行jar包
ENTRYPOINT ["java", "-jar","/qinlinopen-docker-file.jar"]
# 指定维护者的名字
MAINTAINER mrtao`