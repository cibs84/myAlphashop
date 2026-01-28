import { MESSAGE_KEYS } from "./message-keys";

type RawType = typeof MESSAGE_KEYS;

export type TranslationMap = {
  readonly [K in keyof RawType]: {
    readonly [T in keyof RawType[K]]: string;
  };
};
