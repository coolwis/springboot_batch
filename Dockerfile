FROM openjdk:11

# container image 안으로 파일 복사함.
ADD ./build/libs/*.jar appBatch.jar
ADD ./batchRun.sh batchRun.sh