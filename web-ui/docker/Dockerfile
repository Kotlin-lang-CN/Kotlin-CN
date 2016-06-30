# Kotlin China web-ui Dockerfile
# For more information please visit Kotlin China http://kotlin-cn.tech'
# This docker file is build for deploying release
# Author: chpengzh
FROM nginx

COPY web-ui/src/app /usr/share/nginx/html

EXPOSE 80

CMD nginx -g 'daemon off;'