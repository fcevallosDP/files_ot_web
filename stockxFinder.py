from selenium import webdriver
#import myBD
import random
import sys
from time import sleep

valorRetorno = "inicial"
try:
    driver = webdriver.Chrome('./chromedriver.exe')
    driver.get("https://stockx.com/")
    # se registra el item StyleId a buscar y espera obtener resultados entre 3 y 5 segs
    finder = driver.find_elements_by_xpath('//*[@id="home-search"]')
    if len(finder) == 0:
       finder = driver.find_elements_by_xpath('//*[@id="site-search"]')
    if len(finder) > 0:
       finder[0].clear()
       finder[0].send_keys(shoeToFind)
       finder[0].send_keys(u'\ue007')
       sleep(random.uniform(3.0, 5.0))

    # toma el primer elemento de los resultados
    options = driver.find_elements_by_xpath('//div[@class="tile browse-tile updated"]')

    for shoe in options:
        foundDiv = shoe.find_element_by_xpath('//div[@data-testid="product-tile"]')
        foundHref = foundDiv.find_element_by_tag_name('a').get_attribute("href")
        if len(foundHref) > 0:
           driver.get(foundHref)
           sleep(random.uniform(3.0, 5.0))
           #espera que carguen los datos para rastrear los precios por talla
           dataDictionary = driver.execute_script('return window.preLoaded')
           dataProducts = []
           dataProducts = dataDictionary["product"]["children"]
           for key, value in dataProducts.items():
               valorRetorno = str(value["market"]["highestBid"])
               #myBD.addProductDetail(shoeToFind, value["sizeLocale"], value['shoeSize'], str(value["market"]["lowestAsk"]), str(value["market"]["highestBid"]))
               break
except ValueError:
    valorRetorno= "algo salió mal"
sys.exit(valorRetorno)