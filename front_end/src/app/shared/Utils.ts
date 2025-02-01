export function isServerErrorStatus(status: number): boolean {
  return status === 0 || status.toString().startsWith("50");
}
