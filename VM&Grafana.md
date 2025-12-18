# Aceder VM
ssh xpto@deti-tqs-18.ua.pt

-----------------

# Aceder ao site
http://deti-tqs-18.ua.pt:8080  

-----------------

# Abrir Grafana
http://deti-tqs-18.ua.pt:8080  

## Com vm
ssh -L 3000:localhost:3000 -L 9090:localhost:9090 xtpo@deti-tqs-18.ua.pt
http://localhost:3000

------------------

# Reniciar app
vm: sudo systemctl restart minefornow
