# PasswordManager
Android application to securely manage accounts.

## Features
- Create, search, edit and delete accounts
- Generate random passwords
- Copy password to clipboard

## Notes
This implementation uses advanced encryption algorithms to store data securely.
A master password is used to encrypt and decrypt the data, and is not stored anywhere directly.

Each time a new master password is set, a safe web request is made to a server (passwords are not sent directly).

## Languages
The language is determined by the system language. If the system language is not supported, the default language is used.
- English (appeared in `v0.3.0`, **default** since then)
- Spanish (appeared in `v0.3.0`)
- French (initial language, from `v0.1.0` to `v0.2.0`)

## License
This project is licensed under the GPL-3.0 License - see the [LICENSE](LICENSE) file for details.
