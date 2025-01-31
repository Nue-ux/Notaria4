# Notaria4

Notaria4 és una aplicació per a Android dissenyada per gestionar cites notarials. L'aplicació permet als usuaris programar, visualitzar i eliminar cites, així com rebre notificacions de les cites programades.

## Funcionalitats

- **Programar cites**: Els usuaris poden programar cites seleccionant un notari, sala, data i hora.
- **Visualitzar cites**: Els usuaris poden veure totes les cites programades en un tauler.
- **Eliminar cites**: Els usuaris poden eliminar cites des del tauler.
- **Notificacions**: Els usuaris reben notificacions per a les cites programades.

## Instal·lació

1. Clona el repositori:
    ```sh
    git clone https://github.com/Nue-ux/notaria4.git
    ```
2. Obre el projecte a Android Studio.
3. Compila el projecte per descarregar totes les dependències.

## Ús

1. **Programar una cita:**
    - Navega a la pantalla principal.
    - Omple els detalls de la cita (notari, sala, data, hora, descripció).
    - Fes clic al botó "Enviar" per desar la cita.

2. **Visualitzar cites:**
    - Navega a la pantalla del tauler.
    - Es mostraran totes les cites programades.

3. **Eliminar una cita:**
    - A la pantalla del tauler, fes clic al botó "Eliminar" al costat de la cita que vulguis suprimir.

4. **Rebre notificacions:**
    - Es rebran notificacions per a les cites programades.

## Estructura del projecte

- `MainActivity.kt`: L'activitat principal que configura la navegació.
- `DatabaseHelper.kt`: Gestiona la creació i la versió de la base de dades.
- `HomeFragment.kt`: Fragment per programar cites.
- `DashboardFragment.kt`: Fragment per visualitzar i eliminar cites.
- `NotificationsFragment.kt`: Fragment per visualitzar notificacions.
- `DashboardReservationAdapter.kt`: Adaptador per mostrar cites al tauler.
- `NotificationAdapter.kt`: Adaptador per mostrar notificacions.

## Esquema de la base de dades

- **Taula d'Usuaris:**
    - `id`: INTEGER PRIMARY KEY AUTOINCREMENT
    - `name`: TEXT
    - `email`: TEXT
    - `date`: TEXT
    - `time`: TEXT
    - `description`: TEXT

- **Taula de Notificacions:**
    - `id`: INTEGER PRIMARY KEY AUTOINCREMENT
    - `title`: TEXT
    - `content`: TEXT

## Dependències

- AndroidX
- RecyclerView
- SQLite

## Llicència

Aquest projecte està llicenciat sota la llicència MIT. Consulta el fitxer `LICENSE` per a més detalls.
