import { MESSAGE_KEYS } from './message-keys';

type ValueOf<T> = T[keyof T];

type Keys = typeof MESSAGE_KEYS;

export type MsgKey = {
  [K in keyof Keys]: ValueOf<Keys[K]>;
}[keyof Keys];
