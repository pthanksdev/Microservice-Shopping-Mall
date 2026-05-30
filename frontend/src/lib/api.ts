import axios from 'axios';

// Use the environment variable for the product service URL, with a fallback for local development.
// The product service URL is defined in infrastructure/k8s/configmaps/app-config.yml as http://product-service:8082
const API_BASE_URL = process.env.NEXT_PUBLIC_PRODUCT_SERVICE_URL || 'http://localhost:8080';

const api = axios.create({
  baseURL: API_BASE_URL,
});

export default api;
