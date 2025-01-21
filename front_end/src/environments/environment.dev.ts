// My default 'ng serve' configuration

const HOST = 'localhost';
const PORT = '8084'; // NGINX Container Port

export const environment = {
  production: false,
  host: HOST,
  port: PORT,
  apiUrl: `http://${HOST}:${PORT}/api`
};
