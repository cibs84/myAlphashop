// My default 'ng serve' configuration

const HOST = 'localhost';
const PORT = '4300'; // NGINX Port

export const environment = {
  production: false,
  host: HOST,
  port: PORT,
  apiUrl: `http://${HOST}:${PORT}/api`
};
