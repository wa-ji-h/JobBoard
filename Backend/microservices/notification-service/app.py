from flask import Flask, jsonify
import py_eureka_client.eureka_client as eureka_client

app = Flask(__name__)

eureka_client.init(
    eureka_server="http://localhost:8761/eureka/",
    app_name="NOTIFICATION-SERVICE",
    instance_port=8085,
    instance_host="localhost"
)

@app.route("/")
def home():
    return jsonify(message="NOTIFICATION-SERVICE fonctionne !")

@app.route("/health")
def health():
    return jsonify(status="UP")

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8085)