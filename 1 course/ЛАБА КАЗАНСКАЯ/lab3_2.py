import numpy as np
import matplotlib.pyplot as plt

# Целевая функция
def f(x, y):
    return 25*x**2 + (y + 4)**2

# Исходные точки и приращения
x_base = np.array([5.0, 4.0])
deltas = np.array([1.0, 1.0])

# Шаг исследовательского поиска
# 1. Исследование по x1
x1_plus = x_base + np.array([deltas[0], 0])
f_base = f(*x_base)
f1 = f(*x1_plus)
if f1 < f_base:
    x1_new = x1_plus
else:
    x1_new = x_base - np.array([deltas[0], 0])
# 2. Исследование по x2
x2_plus = x1_new + np.array([0, deltas[1]])
f2 = f(*x2_plus)
if f2 < f1 and f2 < f_base:
    x_exp = x2_plus
else:
    x_exp = x1_new - np.array([0, deltas[1]])

# Паттерн-ход
x_pat = x_exp + (x_exp - x_base)
f_pat = f(*x_pat)
if f_pat < f(*x_exp):
    x_new = x_pat
else:
    x_new = x_exp

# Настройка сетки для контура
xi = np.linspace(0, 10, 300)
yi = np.linspace(-2, 6, 300)
X, Y = np.meshgrid(xi, yi)
Z = f(X, Y)

# Рисуем контур
plt.figure(figsize=(7,5))
plt.contour(X, Y, Z, levels=30)
plt.scatter(*x_base, s=80)  # базовая
plt.scatter(*x1_plus, s=50)
plt.scatter(*x1_new, s=50)
plt.scatter(*x2_plus, s=50)
plt.scatter(*x_exp, s=80)
plt.scatter(*x_pat, s=80)

# Добавляем стрелки
plt.annotate('', xy=x1_plus, xytext=x_base, arrowprops=dict(arrowstyle='->'))
plt.annotate('', xy=x1_new, xytext=x1_plus, arrowprops=dict(arrowstyle='->'))
plt.annotate('', xy=x2_plus, xytext=x1_new, arrowprops=dict(arrowstyle='->'))
plt.annotate('', xy=x_exp, xytext=x_base, arrowprops=dict(arrowstyle='->', linestyle='--'))
plt.annotate('', xy=x_pat, xytext=x_exp, arrowprops=dict(arrowstyle='->', linestyle=':'))

# Подписи
plt.text(*x_base, '  x_base', va='bottom')
plt.text(*x1_plus, '  x1(+)', va='bottom')
plt.text(*x1_new, '  x1(-/new)', va='bottom')
plt.text(*x2_plus, '  x2(+)', va='bottom')
plt.text(*x_exp, '  x_exp', va='bottom')
plt.text(*x_pat, '  x_pat', va='bottom')

plt.xlabel('x1')
plt.ylabel('x2')
plt.title('Визуализация первого шага Хука–Дживса')
plt.grid(True)
plt.tight_layout()
plt.show()