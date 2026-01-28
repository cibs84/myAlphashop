export interface CardFieldsConfig<T> {
  actionId: keyof T,
  title?: keyof T,
  subtitle?: keyof T,
  text?: keyof T,
  img?: keyof T,
  status?: keyof T
}
