// My default 'ng build' configuration

const HOST = '34.124.165.164';
const PORT = '80'; // NGINX Port

export const environment = {
  production: true,
  apiUrl: `http://${HOST}:${PORT}/api`
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.
