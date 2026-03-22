Run with bind mount:

```bash
docker build -t bai10 .
docker run --rm -p 8080:80 -v ${PWD}:/var/www/html bai10
```
