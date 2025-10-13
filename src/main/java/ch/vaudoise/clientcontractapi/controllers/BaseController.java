package ch.vaudoise.clientcontractapi.controllers;

/**
 * Base controller class providing a centralized API version prefix.
 * All other controller classes should reference the {@link #API_V1} constant
 * to include the versioning prefix in their {@code @RequestMapping} paths.
 *
 * Example usage in a controller:
 * 
 * {@code
 * @RestController
 * @RequestMapping(BaseController.API_V1 + "/clients")
 * public class ClientController {
 *     // ...
 * }
 * }
 * </pre>
 *
 * @see ch.vaudoise.clientcontractapi.controllers.ClientController
 * @see ch.vaudoise.clientcontractapi.controllers.ContractController
 */
public abstract class BaseController {

    /**
     * API version prefix used in all request mappings.
     * Update this constant to change the version globally.
     */
    public static final String API_V1 = "/api/v1";

    // Prevent instantiation
    protected BaseController() {}
}
