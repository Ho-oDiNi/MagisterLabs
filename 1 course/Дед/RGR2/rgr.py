import numpy as np
import matplotlib.pyplot as plt
from scipy.signal import hilbert, welch
from scipy.optimize import minimize

# Параметры моделирования
FS = 100  # Частота дискретизации (Гц)
DURATION = 5.0  # Длительность сигнала (с)
FREQ_RANGE = (1, 20)  # Диапазон частот (Гц)
SPEED = 3500  # Скорость сейсмических волн (м/с)
ARRAY_RADIUS = 500  # Радиус размещения датчиков (м)
NOISE_LEVEL = 0.15  # Уровень шума

def generate_blast_signal():
    """Генерация сигнала, похожего на промышленный взрыв"""
    t = np.linspace(0, DURATION, int(FS*DURATION), endpoint=False)
    
    # Основной низкочастотный компонент
    main_freq = np.random.uniform(FREQ_RANGE[0], FREQ_RANGE[0]+5)
    main_signal = np.exp(-1.2*t) * np.sin(2*np.pi*main_freq*t)
    
    # Высокочастотные компоненты
    for _ in range(3):
        freq = np.random.uniform(FREQ_RANGE[0]+5, FREQ_RANGE[1])
        main_signal += 0.5*np.exp(-2.5*t) * np.sin(2*np.pi*freq*t + np.random.uniform(0, np.pi))
    
    # Добавление шума
    main_signal += NOISE_LEVEL * np.random.normal(size=len(t))
    
    return t, main_signal

def create_sensor_array(n_sensors, radius):
    """Создание массива датчиков"""
    angles = np.linspace(0, 2*np.pi, n_sensors, endpoint=False)
    x = radius * np.cos(angles)
    y = radius * np.sin(angles)
    return angles, x, y

def add_delays(t, signal, sensor_angles, true_direction, source_distance):
    """Добавление задержек сигнала для каждого датчика относительно центра массива"""
    signals = []
    center_delay = source_distance / SPEED  # Задержка до центра массива
    
    # Корректировка направления на 180 градусов
    corrected_direction = (true_direction + np.pi) % (2*np.pi)
    
    for angle in sensor_angles:
        # Разность хода волны до датчика относительно центра
        delta = ARRAY_RADIUS * np.cos(angle - corrected_direction)  # Используем скорректированное направление
        sensor_delay = center_delay + delta / SPEED
        
        # Сдвиг сигнала с интерполяцией
        n_shift = int(sensor_delay * FS)
        shifted = np.roll(signal, n_shift)
        
        # Добавление уникального шума
        noise = NOISE_LEVEL * np.random.normal(size=len(shifted))
        signals.append(shifted + noise)
    
    return np.array(signals)

def calculate_dnd(signals, sensor_angles, n_points=360):
    """Расчет коэффициента направленного действия (КНД)"""
    n_sensors = len(sensor_angles)
    theta_grid = np.linspace(0, 2*np.pi, n_points, endpoint=False)
    dnd = np.zeros(n_points)
    
    # Преобразование Гильберта для аналитических сигналов
    analytic_signals = [hilbert(s) for s in signals]
    
    for i, theta in enumerate(theta_grid):
        # Расчет ожидаемых фазовых сдвигов
        phase_shifts = ARRAY_RADIUS/SPEED * np.cos(sensor_angles - theta)
        
        # Компенсация задержек и суммирование
        compensated = []
        for j in range(n_sensors):
            # Фазовый сдвиг во временной области
            shift_samples = int(phase_shifts[j] * FS)
            shifted = np.roll(analytic_signals[j], shift_samples)
            compensated.append(shifted)
        
        # Суммирование с когерентным накоплением
        total_signal = np.sum(np.abs(np.sum(compensated, axis=0))**2)
        dnd[i] = total_signal
    
    # Нормализация
    dnd /= np.max(dnd)
    return theta_grid, dnd

def find_peak_direction(theta, dnd):
    """Нахождение направления на источник по максимуму КНД"""
    peak_idx = np.argmax(dnd)
    return theta[peak_idx]

# Основное исследование
n_sensors_range = range(2, 7)  # От 2 до 7 датчиков
true_direction = np.random.uniform(0, 2*np.pi +np.pi)  # Единое истинное направление для всех датчиков
source_distance = 2000  # Расстояние до источника (м)

for n_sensors in n_sensors_range:
    # Создание массива датчиков
    sensor_angles, x, y = create_sensor_array(n_sensors, ARRAY_RADIUS)
    
    # Генерация сигнала
    t, source_signal = generate_blast_signal()
    
    # Моделирование сигналов на датчиках с задержками
    sensor_signals = add_delays(t, source_signal, sensor_angles, true_direction, source_distance)
    
    # Расчет КНД
    theta, dnd = calculate_dnd(sensor_signals, sensor_angles)
    est_direction = find_peak_direction(theta, dnd)
    
    # Визуализация
    plt.figure(figsize=(10, 8))
    ax = plt.subplot(111, polar=True)
    ax.plot(theta, dnd, 'b-', linewidth=2, label='КНД')
    ax.plot([true_direction, true_direction], [0, 1], 'r--', 
            linewidth=2, label='Истинное направление')
    ax.plot([est_direction, est_direction], [0, 1], 'g-.', 
            linewidth=2, label='Определенное направление')
    
    # Отметки датчиков
    for angle in sensor_angles:
        ax.plot([angle, angle], [0, 0.1], 'ko-', markersize=8)
    
    ax.set_title(f'Диаграмма направленности\n{n_sensors} датчиков', pad=20)
    ax.legend(loc='upper right')
    plt.show()
    
    # Расчет ошибки
    error = min(abs(est_direction - true_direction), 
              2*np.pi - abs(est_direction - true_direction))
    print(f"{n_sensors} датчиков: ошибка {np.degrees(error):.2f}°")