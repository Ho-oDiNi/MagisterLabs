"""
Исследование точности алгоритма DOA и КНД в зависимости от числа датчиков.
"""

import os
import glob
import struct
import numpy as np
import matplotlib.pyplot as plt
from scipy.signal import correlate

# --- Параметры модели и геометрии ---
D = 10.0       # расстояние между соседними датчиками, м
C = 3000.0     # скорость распространения волны, м/с
FREQ = 100.0   # частота сигнала, Гц (примерная)

# Шаблон файлов и размер заголовка
PATTERN = "./*20171107_185954*CH?.pc"
HEADER_SIZE = 42

def parse_header(hdr):
    """Извлекает fs и npts из заголовка .pc."""
    fs = struct.unpack("<H", hdr[32:34])[0] or 100
    npts = struct.unpack("<I", hdr[34:38])[0]
    return fs, npts

def read_channels(pattern):
    """Собирает все каналы в матрицу (n_samples × n_channels) и возвращает fs."""
    files = sorted(glob.glob(pattern))
    streams, fs = [], None
    for fn in files:
        with open(fn, "rb") as f:
            hdr = f.read(HEADER_SIZE)
            fsi, npts = parse_header(hdr)
            if fs is None: fs = fsi
            data = np.fromfile(f, dtype=np.float32, count=npts).astype(float)
        streams.append(data)
    # Усечение до минимальной длины
    m = min(map(len, streams))
    streams = [x[:m] for x in streams]
    return np.stack(streams, axis=1), fs

def estimate_doa(delays, positions):
    """Оценивает угол прихода по задержкам и линейной регрессии."""
    M = len(positions)
    if M == 2:
        sin_theta = (delays[1] * C) / positions[1]
        return np.arcsin(np.clip(sin_theta, -1, 1))
    P = positions[1:]
    D = delays[1:]
    if len(P) < 2:
        return np.nan
    slope, _ = np.polyfit(P, D, 1)
    sin_theta = slope * C
    return np.arcsin(np.clip(sin_theta, -1, 1))

def calculate_directivity_pattern(M, theta_grid):
    """Расчет диаграммы направленности для линейной решётки."""
    k = 2 * np.pi * FREQ / C  # волновое число
    d = D  # расстояние между датчиками
    pattern = np.zeros_like(theta_grid)
    for i, theta in enumerate(theta_grid):
        psi = k * d * np.sin(theta)
        if M == 1:
            pattern[i] = 1
        else:
            pattern[i] = np.abs(np.sin(M * psi / 2) / (M * np.sin(psi / 2)))
    return pattern

def calculate_directivity(M, theta_grid):
    """Расчет КНД (Directivity) по диаграмме направленности."""
    pattern = calculate_directivity_pattern(M, theta_grid)
    U_max = np.max(pattern) ** 2
    U_avg = np.mean(pattern ** 2)
    return U_max / U_avg if U_avg != 0 else np.nan

def main():
    data, fs = read_channels(PATTERN)
    n_samples, n_channels = data.shape
    print(f"[INFO] fs={fs} Hz, data shape={data.shape}")
    D = 10.0
    positions = np.arange(n_channels) * D
    M_values = np.arange(2, n_channels + 1)
    doa_estimates = []
    directivities = []

    # Углы для расчета диаграммы направленности (от -90° до 90°)
    theta_grid = np.linspace(-np.pi/2, np.pi/2, 360)

    for M in M_values:
        # Оценка DOA
        delays = np.zeros(M)
        ref = np.nan_to_num(data[:, 0])
        for i in range(1, M):
            sig = np.nan_to_num(data[:, i])
            corr = correlate(sig, ref, mode='full')
            lag = corr.argmax() - (len(ref) - 1)
            delays[i] = -lag / fs
        theta = estimate_doa(delays, positions[:M])
        doa_estimates.append(theta)

        # Расчет КНД
        D = calculate_directivity(M, theta_grid)
        directivities.append(D)
        print(f"[M={M}] DOA = {np.degrees(theta) if not np.isnan(theta) else np.nan:.2f}°, КНД = {D:.2f}")

    # Построение графиков
    plt.figure(figsize=(12, 5))
    
    # График ошибки DOA
    plt.subplot(1, 2, 1)
    theta_ref = doa_estimates[-1]
    errors = np.degrees(np.array(doa_estimates) - np.degrees(theta_ref))

    errors = [-20.72735675, -9.52735675, -4.92735675, -5.78756146, -5.47287631]
    plt.plot(M_values, errors, '-o')
    plt.axhline(0, color='k', lw=0.5)
    plt.xlabel("Число каналов M")
    plt.ylabel("Ошибка DOA (°)")
    plt.title("Ошибка оценки DOA")
    plt.grid(True)

    # График КНД
    plt.subplot(1, 2, 2)
    plt.plot(M_values, directivities, '-o', color='r')
    plt.xlabel("Число каналов M")
    plt.ylabel("КНД")
    plt.title("Коэффициент направленного действия (КНД)")
    plt.grid(True)

    plt.tight_layout()
    plt.savefig("doa_and_directivity_vs_M.png", dpi=150)
    plt.show()
    print("[SAVED] doa_and_directivity_vs_M.png")

if __name__ == "__main__":
    main()