#!/bin/bash

date_str=$(date +"%Y-%m-%d")    # Получаем дату
user_name=$(whoami)             # Получаем имя учетной записи
domain_name=$(hostname)         # Получаем доменное имя

# Получаем информацию о процессоре
processor_model=$(grep "model name" /proc/cpuinfo | awk '{print $3,$4,$5,$6,$7}' | head -1)     #Модель
processor_arch=$(lscpu | grep Architecture | awk '{print $2}')                                  #Архитектура
processor_frequency=$(grep "cpu MHz" /proc/cpuinfo | awk '{print $4}' | awk '{print $1}' | head -1)       #Тактовая частота
processor_cores=$(grep "cpu cores" /proc/cpuinfo | awk '{print $4}' | head -1)            # Количество ядер
processor_threads=$(grep "siblings" /proc/cpuinfo | awk '{print $3}' | head -1 )          # Всего потоков
let "processor_thread_per_core = $processor_threads / $processor_cores"                   # Потоков на ядро


# Получаем информацию об оперативной памяти
memory_total=$(free -m | grep Mem | awk '{print $2}')
memory_available=$(free -m | grep Mem | awk '{print $4}')

# Получаем информацию о жестком диске
disk_total=$(df -h / | grep -v Filesystem | awk '{print $2}')
disk_available=$(df -h / | grep -v Filesystem | awk '{print $4}')
disk_mounted=$(df -h / | grep -v Filesystem | awk '{print $1}')
swap_total=$(free -m | grep Swap | awk '{print $2}')
swap_available=$(free -m | grep Swap | awk '{print $4}')


echo ""
echo "--------------------------------------------"
echo "Дата: $date_str"
echo "Имя учетной записи: $user_name"
echo "Доменное имя ПК: $domain_name"
echo ""
echo "Процессор:"
echo "  Модель: $processor_model"
echo "  Архитектура: $processor_arch"
echo "  Тактовая частота: $processor_frequency MHz"
echo "  Количество ядер: $processor_cores"
echo "  Количество потоков на одно ядро: $processor_thread_per_core"
echo ""
echo "Оперативная память:"
echo "  Всего: $memory_total MB"
echo "  Доступно: $memory_available MB"
echo ""
echo "Жесткий диск:"
echo "  Всего: $disk_total"
echo "  Доступно: $disk_available"
echo "  Смонтировано в корневую директорию: $disk_mounted"
echo "  SWAP всего: $swap_total MB"
echo "  SWAP доступно: $swap_available MB"

# Получаем информацию о сетевых интерфейсах
interface_count=$(ls /sys/class/net | wc -l)
interface_list=$(ls /sys/class/net)
echo "Сетевые интерфейсы:"
echo "  Количество сетевых интерфейсов: $interface_count"

i=1
for interface in $interface_list; do
  mac_address=$(cat /sys/class/net/$interface/address)
  ip_address=$(ip addr show $interface | grep 'inet ' | awk '{print $2}')
  # speed=$(ethtool $interface | grep "Speed:" | awk '{print $2}')
  # speedtest_output=$(speedtest-cli --no-upload | grep "Download:" | awk '{print $2}')
  echo "---------------------------"
  echo "| $i |"
  echo "| $interface |"
  echo "| $ip_address |"
  echo "| $mac_address |"
  
  i=$((i+1))
done

echo "---------------------------"
echo ""