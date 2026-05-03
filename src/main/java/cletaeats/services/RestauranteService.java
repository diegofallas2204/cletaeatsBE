package cletaeats.services;

import cletaeats.models.Restaurante;
import cletaeats.repositories.RestauranteRepository;
import java.sql.SQLException;
import java.util.List;

public class RestauranteService {
    private final RestauranteRepository repository = new RestauranteRepository();

    public List<Restaurante> obtenerTodos() throws SQLException {
        // Aquí se podría implementar lógica adicional (ej: validaciones)
        return repository.listarTodos();
    }
}