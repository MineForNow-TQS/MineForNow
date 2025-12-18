# Deployment Setup Instructions

This document guides you through setting up the deployment environment on your VM.

## 1. Prerequisites (On the VM)

Ensure you have Java 21 installed on your VM, as the application is configured for it.

```bash
# Check Java version
java -version

# If needed, install OpenJDK 21 (Ubuntu/Debian)
sudo apt update
sudo apt install openjdk-21-jdk
```

## 2. Directory Structure

The workflow assumes a specific directory structure. Run these commands on your VM to prepare the folders:

```bash
# Workflow uses a temporary deploy folder
mkdir -p ~/deploy-temp

# And expects the app.jar to be in your home directory eventually (or wherever you configure)
```

## 3. Systemd Service Configuration

To keep the application running in the background and restart it on boot, use `systemd`.

1.  **Create the service file:**

    ```bash
    sudo nano /etc/systemd/system/myapp.service
    ```

2.  **Paste the following configuration:**

    Replace `YOUR_USERNAME` with your actual username on the VM (e.g., `daniel.f.nunes`).

    ```ini
    [Unit]
    Description=Internal TQS App (Spring + React)
    After=network.target postgresql.service 
    # ^ Add postgresql.service if you are running a local DB, otherwise remove it.

    [Service]
    User=YOUR_USERNAME
    Type=simple
    # Command to run the JAR
    ExecStart=/usr/bin/java -jar /home/YOUR_USERNAME/app.jar
    
    # Handle graceful shutdown
    SuccessExitStatus=143
    
    # Auto-restart if it crashes
    Restart=always
    RestartSec=5

    [Install]
    WantedBy=multi-user.target
    ```

3.  **Enable and Start the Service:**

    ```bash
    # Reload systemd to verify the new file
    sudo systemctl daemon-reload

    # Enable the service to start on boot
    sudo systemctl enable myapp

    # Start the service immediately (note: it might fail if app.jar isn't there yet, which is fine)
    sudo systemctl start myapp
    ```

## 4. GitHub Secrets

For the GitHub Action to work, go to your repository **Settings > Secrets and variables > Actions** and add:

*   `SSH_HOST`: The IP address of your VM.
*   `SSH_USERNAME`: The username you use to SSH into the VM.
*   `SSH_KEY`: Your private SSH key (open your local `id_rsa` or similar and copy the content).


´´´
. SSH_HOST (O endereço da VM)
É o endereço que usas para te ligares à máquina. Tentas-te usar deti-tqs-18.ua.pt anteriormente.

Valor: deti-tqs-18.ua.pt (ou o IP correspondente, se o hostname não funcionar fora da VPN).
Nota: Se precisares do IP e estiveres ligado na VM, podes correr o comando hostname -I.
2. SSH_USERNAME (O teu utilizador)
É o nome de utilizador com que fazes login na VM.

Valor: daniel.f.nunes
(Visto que usaste User=daniel.f.nunes no ficheiro de serviço e tentaste fazer SSH com esse user).
3. SSH_KEY (A tua chave privada)
Esta é a "password" digital que o GitHub vai usar para entrar na VM em teu nome. Está no teu computador local (onde estamos agora).

Para obteres o conteúdo da chave, corre este comando no terminal (não te preocupes, eu não vou guardar nem ver o output se não mo pedires, mas copia apenas para o GitHub):

bash
# Se a tua chave padrão for id_rsa:
cat ~/.ssh/id_rsa
# OU se usares outro tipo (ex: ed25519):
cat ~/.ssh/id_ed25519
Instruções Importantes:

O comando vai cuspir um texto enorme que começa com -----BEGIN OPENSSH PRIVATE KEY----- e acaba em -----END OPENSSH PRIVATE KEY-----.
Copia TUDO, incluindo essas linhas de início e fim.
Cola na caixa de "Value" no segredo do GitHub.
```