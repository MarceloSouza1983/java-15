package br.com.codenation.service;

import java.util.*;
import java.util.stream.Collectors;

import br.com.codenation.model.OrderItem;
import br.com.codenation.model.Product;
import br.com.codenation.repository.ProductRepository;
import br.com.codenation.repository.ProductRepositoryImpl;

public class OrderServiceImpl implements OrderService {

	private ProductRepository productRepository = new ProductRepositoryImpl();

	/**
	 * Calculate the sum of all OrderItems */
	@Override
	public Double calculateOrderValue(List<OrderItem> items) {

		Map<Long, Double> lista = items.stream()
				.collect(Collectors.toMap(OrderItem::getProductId, orderItem -> {
					if (productRepository.findById(orderItem.getProductId()).get().getIsSale()) {

						return (((productRepository.findById(orderItem.getProductId()).get()
								.getValue() / 100) * 80 ) * orderItem.getQuantity());
					} else {
						return (productRepository.findById(orderItem.getProductId()).get().getValue()) * orderItem.getQuantity();
					}
				}));

		return lista.entrySet().stream().mapToDouble(longDoubleEntry -> longDoubleEntry.getValue()).sum();

	}

	/**
	 * Map from idProduct List to Product Set */
	@Override
	public Set<Product> findProductsById(List<Long> ids) {
		return ids.stream()
				.map(id -> (productRepository.findById(id).orElse(null)))
				.filter(product -> product !=null).collect(Collectors.toSet());
	}

	/**
	 * Calculate the sum of all Orders(List<OrderIten>) */
	@Override
	public Double calculateMultipleOrders(List<List<OrderItem>> orders) {

		List<Double> totalOrders = new ArrayList<>();

		orders.stream()
				.forEach(orderItemList -> orderItemList.stream()
						.forEach(orderItem -> {
							if (productRepository.findById(orderItem.getProductId()).get().getIsSale()) {
								totalOrders.add(((productRepository.findById(orderItem.getProductId())
										.get().getValue() / 100) * 80 ) * orderItem.getQuantity());

							} else {
								totalOrders.add(productRepository.findById(orderItem.getProductId())
										.get().getValue() * orderItem.getQuantity());
							}
						}));

		return totalOrders.stream().mapToDouble(value -> value.doubleValue()).sum();
	}

	/**
	 * Group products using isSale attribute as the map key */
	@Override
	public Map<Boolean, List<Product>> groupProductsBySale(List<Long> productIds) {

		List <Product> produtosComDesconto = new ArrayList<>();
		List <Product> produtosSemDesconto = new ArrayList<>();

		productIds.stream().forEach(x -> {
			if (productRepository.findById(x).get().getIsSale()) {
				produtosComDesconto.add(productRepository.findById(x).get());
			} else {
				produtosSemDesconto.add(productRepository.findById(x).get());
			}
		});

		Map<Boolean, List<Product>> produtos = new HashMap<Boolean, List<Product>>();

		produtos.put(true, produtosComDesconto);
		produtos.put(false, produtosSemDesconto);

		return produtos;
	}

}