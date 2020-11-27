import mongoose from "mongoose";
import config from "./index.js";

const CONNECTION_URL = `mongodb://${config.db.url}/${config.db.name}`;

mongoose.connect(CONNECTION_URL, {
  useNewUrlParser: true,
  useUnifiedTopology: true,
  useCreateIndex: true,
  useFindAndModify: false 
});

mongoose.connection.on("connected", () => {
  console.info("Mongo has connected successfully");
});
mongoose.connection.on("reconnected", () => {
  console.info("Mongo has reconnected");
});
mongoose.connection.on("error", (error) => {
  console.info("Mongo connection has an error", error);
  mongoose.disconnect();
});
mongoose.connection.on("disconnected", () => {
  console.info("Mongo connection is disconnected");
});