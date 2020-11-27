import mongoose from "mongoose";
import config from "./index.js";
import {logger} from "../app.js";

const CONNECTION_URL = `mongodb://${config.db.url}/${config.db.name}`;

mongoose.connect(CONNECTION_URL, {
  useNewUrlParser: true,
  useUnifiedTopology: true,
  useCreateIndex: true,
  useFindAndModify: false 
});

mongoose.connection.on("connected", () => {
    logger.info("Mongo has connected successfully");
});
mongoose.connection.on("reconnected", () => {
    logger.info("Mongo has reconnected");
});
mongoose.connection.on("error", (error) => {
    logger.info("Mongo connection has an error", error);
    mongoose.disconnect();
});
mongoose.connection.on("disconnected", () => {
    logger.info("Mongo connection is disconnected");
});