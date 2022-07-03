# Configurable Microservice Benchmarking Application

This Java project was developed at the University of Hamburg over the course of a seminar with the topic of "Quality-centric Continuous Software Engineering 2022" held by Dr.-Ing. André van Hoorn.

## Inspiration

After studying benchmarking applications [TeaStore](https://github.com/DescartesResearch/TeaStore), [Train-Ticket](https://github.com/FudanSELab/train-ticket) and [Sock Shop](https://github.com/microservices-demo/microservices-demo), we thought that the given architectures were very ridged and not customizable.

## Goal of this Project

So we designed CMS Benchmarking App. This (almost nano-) microservice consists only of one endpoint. The endpoint, it's response or transitive webserver call and the response delay (simulating computation) are all configurable.

The idea behind this is, to have just one docker image (pick the latest here [hub.docker.com/cmd-benchmarking-app](https://hub.docker.com/r/vdwps/cmd-benchmarking-app/tags)), in a docker-compose, configure the microservice (mocked) behaviour and optionally add it to the reverse proxy and run it.

### Configurable Properties

| Property                        | Type    | Meaning                                                                                |
|---------------------------------|---------|----------------------------------------------------------------------------------------|
| `port`                          | int     | The port on which the servlet starts                                                   |
| `endpoint`                      | string  | At which the content is provided at                                                    |
|                                 |         |                                                                                        |
| `response`                      | string  | Arbitrary response message (e.g. JSON)                                                 |
| `responseType`                  | string  | The type which is uses for the response (e.g. application/json)                        |
| `forwardCall`                   | string  | The address where the servlet call will try to get the response from                   |
|                                 |         |                                                                                        |
| `appendParamToResponse`         | boolean | Whether the send Params should be appended to the response                             |
| `appendAmountOfCallsToResponse` | boolean | Whether the amount of previous calls to this server should be appended to the response |
|                                 |         |                                                                                        |
| `logging.level.tech.dobler`     | string  | DEBUG to enable some debug logging                                                     |
| `delay`                         | int     | The amount of time in milliseconds the response is withheld (simulated load)           |