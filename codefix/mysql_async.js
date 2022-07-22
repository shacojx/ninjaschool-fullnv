const mysql = require("mysql");

const connection = mysql.createConnection({
  host: "localhost",
  user: "root",
  password: "mypassword",
  database: "lhd",
});

const query = (sql) => {
  return new Promise((resolve, reject) => {
    connection.query(sql, (e, results) => {
      if (e) return reject(e);
      resolve(results);
    });
  });
};

const close = ()=>{
    connection.end();
}
module.exports = { query,close };
