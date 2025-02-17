FROM openjdk:17-jdk-slim

# 필요한 패키지 설치 및 로케일 설정
RUN apt-get update && \
    apt-get install -y locales wget unzip && \
    echo "ko_KR.UTF-8 UTF-8" > /etc/locale.gen && \
    locale-gen && \
    apt-get clean

# Locale 환경변수 설정
ENV LANG=ko_KR.UTF-8
ENV LANGUAGE=ko_KR:ko
ENV LC_ALL=ko_KR.UTF-8

# 애플리케이션 JAR 파일 복사
COPY ./build/libs/DangDangCrew-0.0.1-SNAPSHOT.jar app.jar

# 애플리케이션 실행
ENV SPRING_PROFILES_ACTIVE=dev
CMD ["java", "-jar", "app.jar", "--spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]

