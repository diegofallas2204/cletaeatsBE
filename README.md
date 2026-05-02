# Respqlado Camara — Backend

Proyecto Backend Java (Maven) para CletaEats.

Instrucciones rápidas para inicializar el repositorio local y subirlo a GitHub (PowerShell):

1. Inicializar repo local y hacer commit:

```powershell
cd "C:\Users\ludov\Desktop\Respqlado Camara\Backend\backend"
git init -b main
git add -A
git commit -m "Initial commit"
```

2. Crear el repositorio remoto en GitHub (web) o usando la CLI `gh` y añadir el remote:

```powershell
# SSH
git remote add origin git@github.com:USERNAME/REPO.git

# HTTPS
git remote add origin https://github.com/USERNAME/REPO.git

# Push inicial
git push -u origin main
```

3. Notas de seguridad:
- No subas credenciales ni secretos (archivos .env, certificados, dumps con contraseñas).
- Si hay secretos ya commiteados, rota las credenciales y limpia el historial con herramientas como BFG o git-filter-repo.

4. Contacto:
Si necesitas que cree el repositorio remoto y haga push por ti, proporciona el nombre del repositorio y si debe ser público o privado.

