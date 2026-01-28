export function getByPath(
  obj: unknown,
  path: string
): string | undefined {
  return path.split('.').reduce<any>(
    (acc, key) =>
      acc && typeof acc === 'object' ? acc[key] : undefined,
    obj
  );
}
