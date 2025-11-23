export enum StatusCodes {
  BadRequest = 400, // e.g. malformed request syntax
  NotFound = 404, // item not found (read)
  Conflict = 409, // e.g. item already exists (create)
  UnprocessableEntity = 422, // e.g validation error (create, update)
  Forbidden = 403, // e.g. not erasable item (delete)
  Success = 200,
  Accepted = 202,
  NoContent = 204,
  Unauthorized = 401
};
