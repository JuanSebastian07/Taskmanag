#!/bin/bash
import os

print("Comando : 'git init' ")
os.system('git init')
print("----------------------")

print("Comando : 'git status' ")
os.system("git status")
print("----------------------")

print("Comando : 'git add .' ")
os.system("git add .")
print("----------------------")

print("Comando : 'git config --global user.email' ")
os.system("git config --global user.email juansebastiantobarcollazos@gmail.com")
print("----------------------")

print("Comando : 'git config --global user.name ' ")
os.system("git config --global user.name JuanSebastian07")
print("----------------------")

print("Comando : git commit -m 'Se agrega la app' ")
os.system("git commit -m 'Se agrega la app'")
print("----------------------")

print("Comando : git branch -M main")
os.system("git branch -M main")
print("----------------------")

entrada_Uno = input("Copia tu repositorio remoto -> ")

print(f'Comando -> {entrada_Uno}')
os.system(entrada_Uno)
print("----------------------")

print("Comando : git push -u origin main")
os.system("git push -u origin main")
print("success!")
print("----------------------")

