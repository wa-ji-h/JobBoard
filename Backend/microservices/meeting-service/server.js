
const express = require("express");
const { Eureka } = require("eureka-js-client");

const app = express();
const PORT = Number(process.env.PORT || 8083);

app.get("/", (req, res) => {
  res.json({ service: "MEETING-SERVICE", status: "UP", port: PORT });
});

const client = new Eureka({
  instance: {
    app: "MEETING-SERVICE",
    instanceId: `localhost:meeting-service:${PORT}`,
    hostName: "localhost",
    ipAddr: "127.0.0.1",
    statusPageUrl: `http://localhost:${PORT}/`,
    port: { $: PORT, "@enabled": true },
    vipAddress: "meeting-service",
    dataCenterInfo: {
      "@class": "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
      name: "MyOwn"
    }
  },
  eureka: {
    host: "localhost",
    port: 8761,
    servicePath: "/eureka/apps/"
  }
});

app.listen(PORT, () => {
  console.log(`MEETING-SERVICE démarré sur le port ${PORT}`);
  client.start(error => {
    if (error) console.error("Erreur Eureka :", error);
    else console.log("MEETING-SERVICE enregistré dans Eureka !");
  });
});
