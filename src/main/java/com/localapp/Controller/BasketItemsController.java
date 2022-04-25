package com.localapp.Controller;


import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.localapp.PayloadRequest.OrderRequest;
import com.localapp.PayloadResponse.BasketResponse;
import com.localapp.PayloadResponse.MessageResponse;
import com.localapp.Service.BasketItemsService;
import com.localapp.Model.BasketItems;

@RequestMapping("/customer")
@RestController
@CrossOrigin(origins="http://localhost:3000")
public class BasketItemsController {
    private static final Logger logger = LogManager.getLogger(BasketItemsController.class);

    @Autowired
    BasketItemsService basketItemsService;

    //add to cart
    @PostMapping("/addToBasket/{custId}")
    public ResponseEntity<MessageResponse> addToBasket(@PathVariable (value = "custId") int custId , @RequestBody OrderRequest order) {
        try {
            logger.info("Order Received: {}",order);
            boolean saved = basketItemsService.saveBasketItemToRepo(custId, order);
            if(saved) {
                logger.info("Adding to Basket of CustId: {}",custId);
                return ResponseEntity.ok(new MessageResponse("Product added successfully!"));
            }
            else
                logger.error("Product could not be added!");
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Product could not be added!"));
        }
        catch (Exception e) {
            logger.error("Error Occured while adding product!");
            return ResponseEntity
                     .badRequest()
                    .body(new MessageResponse("Error Occured while adding product!"));
        }
    }

    @GetMapping(value = "/getBasket/{custId}")
    public ResponseEntity<?> getBasketItems(@PathVariable (value = "custId") int custId) {
        try {
            logger.info("Fetching Basket Items for Customer with custId: {}",custId);
            double cost = basketItemsService.calculateDiscountedPrice(custId);
            List<BasketItems> basketItems = basketItemsService.getFilteredBasketItems(custId);
            if(cost<=0 && basketItems==null)
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Basket Items for Customer with CustId: "+custId+" not found!"));
            return ResponseEntity.ok(new BasketResponse(basketItems, cost));
        }
        catch (Exception e) {
            logger.error("Error Occured while obtaining Basket Items of Customer with CustId: {}",custId);
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error Occured while obtaining Basket Items of Customer with CustId: "+custId+"!"));
        }
    }

    @GetMapping(value = "/getBasketById/{basketId}")
    public ResponseEntity<MessageResponse> getBasketItemsById(@PathVariable (value = "basketId") int basketId) {
        try {
            logger.info("Fetching Basket Items Details for BasketItem: {}",basketId);
            BasketItems basketItem = basketItemsService.getBasketById(basketId);
            if(basketItem!=null) {
                logger.info("Basket Item Found!");
                return ResponseEntity.ok(new MessageResponse("Basket Item found!"));
            }
            else
                logger.error("Basket Item could not be Found!");
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Basket Item could not be found!"));
        }
        catch (Exception e) {
            logger.error("Error Occured while finding Basket Item Details with Id: {}!",basketId);
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error Occured while finding Basket Item Details with Id: "+basketId+"!"));
        }
    }

    @RequestMapping(value = "/delete/{basketId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<MessageResponse> delete(@PathVariable(value = "basketId") int basketId) throws IOException {
        try {
            logger.info("Deleting Basket Item with basketId: {}",basketId);
            basketItemsService.deleteBasketItem(basketId);
            return ResponseEntity.ok(new MessageResponse("BasketItem Deleted Successfully!"));
        }
        catch (Exception e)
        {
            logger.error("Error Occured while deleting Basket Item with basketId: {}",basketId);
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: BasketItem could not be deleted!"));
        }
    }

    @GetMapping(value = "/sendInvoice/{custId}")
    public ResponseEntity<?> sendInvoice(@PathVariable (value = "custId") int custId) {
        try {
            logger.info("Sending invoice to Customer with custId: {}",custId);
            return ResponseEntity.ok(new MessageResponse("Payment done successfully!"));
        }
        catch (Exception e) {
            logger.error("Error Occured while sending invoice to Customer with CustId: {}",custId);
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error Occured while sending invoice to Customer with CustId: "+custId+"!"));
        }
    }


}