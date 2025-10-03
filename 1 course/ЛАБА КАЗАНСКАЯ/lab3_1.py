import numpy as np
import matplotlib.pyplot as plt

# Определяем функцию и её градиент для f(x1,x2) = 25*x1^2 + (x2+4)^2
def f(x):
    return 25*x[0]**2 + (x[1] + 4)**2

def grad_f(x):
    return np.array([
        50*x[0],       
        2*(x[1] + 4)  
    ])

# Гессиан
H = np.array([
    [50,  0],
    [ 0,  2]
])

# Параметры метода
x = np.array([5.0, 4.0])  
tol = 1e-1                
max_iter = 100            

# Подготовка к сбору данных
path = [x.copy()]
records = []

# Итерации градиентного спуска
for k in range(max_iter):
    g = grad_f(x)
    gnorm = np.linalg.norm(g)
    num = g.dot(g)
    Hg = H.dot(g)
    den = g.dot(Hg)
    alpha = num / den
    x_new = x - alpha * g

    status = "stop" if gnorm < tol else ""
    records.append((k, x.copy(), g.copy(), gnorm, alpha, x_new.copy(), status))

    if gnorm < tol:
        break
    x = x_new
    path.append(x.copy())

path = np.array(path)

# Вывод табличной сводки расчётов
print(f"\n{'k':>2} | {'x^(k)':>15} | {'grad':>15} | {'||g||':>7} | {'alpha':>7} | {'x^(k+1)':>15} | status")
print("-" * 82)
for k, x_k, g_k, gnorm, alpha, x_next, status in records:
    print(f"{k:2d} | ({x_k[0]:6.2f}, {x_k[1]:6.2f}) |"
          f" ({g_k[0]:6.2f}, {g_k[1]:6.2f}) |"
          f" {gnorm:7.2f} | {alpha:7.3f} |"
          f" ({x_next[0]:6.2f}, {x_next[1]:6.2f}) | {status}")

print(f"\nСame together in {records[-1][0]} iterations")
print(f"Approximate minimum: x = ({x[0]:.6f}, {x[1]:.6f}), f(x) = {f(x):.6e}\n")

# Построение контура функции и траектории
xi = np.linspace(-6, 6, 300)
yi = np.linspace(-10, 10, 300)
X, Y = np.meshgrid(xi, yi)
Z = 25*X**2 + (Y + 4)**2

plt.figure(figsize=(6,5))
cs = plt.contour(X, Y, Z, levels=30, cmap='viridis')
plt.plot(path[:,0], path[:,1], 'o-', color='orange', label='Траектория')
plt.scatter(0, -4, color='red', s=80, label='Точный минимум')
plt.xlabel('x1')
plt.ylabel('x2')
plt.title('Градиентный спуск для f = 25*x1^2 + (x2+4)^2')
plt.legend()
plt.grid()
plt.colorbar(cs, label='f(x1,x2)')
plt.show()