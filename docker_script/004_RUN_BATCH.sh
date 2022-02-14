docker run -itd --name docker_batch_app \
    --network docker_batch_network \
    -e SPRING_PROFILES_ACTIVE=docker \
    -v /app/r100/INFILES:/INFILES \
    docker_batch_app_image


    # application-docker.yml 로 실행 됨
    # source를 실행하는 경로 "/app/r100" , 그 아래 INFILES를 container이미지의 /INFILES 와 링크 시킴.