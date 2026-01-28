// Removes leading/trailing spaces from the string and if null, undefined or empty, returns empty string.
export const strSanitize = (val?: string | null): string => val?.trim() ?? '';
