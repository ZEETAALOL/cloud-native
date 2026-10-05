#!/bin/bash

# Script para actualizar IPs de EC2-2 y EC2-3 en docker-compose.yml
# Pedidos360 - Cloud Native

echo "🔧 Actualizar IPs de otras instancias EC2"
echo "=========================================="

# Leer IPs
read -p "Ingresa la IP PRIVADA de EC2-2 (ec2-mq - RabbitMQ): " EC2_MQ_IP
read -p "Ingresa la IP PRIVADA de EC2-3 (ec2-kafka - Kafka): " EC2_KAFKA_IP

# Validar que no estén vacías
if [ -z "$EC2_MQ_IP" ] || [ -z "$EC2_KAFKA_IP" ]; then
    echo "❌ Las IPs no pueden estar vacías"
    exit 1
fi

echo ""
echo "📝 IPs a configurar:"
echo "   EC2-2 (RabbitMQ): $EC2_MQ_IP"
echo "   EC2-3 (Kafka):    $EC2_KAFKA_IP"
echo ""

read -p "¿Confirmar? (y/n): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "❌ Cancelado"
    exit 1
fi

# Backup del archivo original
cp docker-compose.yml docker-compose.yml.backup
echo "✅ Backup creado: docker-compose.yml.backup"

# Reemplazar IPs en docker-compose.yml
sed -i "s/ec2-mq-ip/$EC2_MQ_IP/g" docker-compose.yml
sed -i "s/ec2-kafka-ip/$EC2_KAFKA_IP/g" docker-compose.yml

echo "✅ IPs actualizadas en docker-compose.yml"
echo ""
echo "🔍 Verificar cambios:"
echo "   grep '$EC2_MQ_IP' docker-compose.yml"
echo "   grep '$EC2_KAFKA_IP' docker-compose.yml"
echo ""
echo "✅ ¡Listo! Ahora puedes ejecutar: ./deploy.sh"
