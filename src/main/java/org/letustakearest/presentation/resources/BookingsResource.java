package org.letustakearest.presentation.resources;

import com.google.code.siren4j.Siren4J;
import org.letustakearest.application.service.BookingService;
import org.letustakearest.domain.Booking;
import org.letustakearest.domain.EntityNotFoundException;
import org.letustakearest.presentation.cache.CacheControlFactory;
import org.letustakearest.presentation.representations.BookingsRepresentationAssembler;
import org.letustakearest.presentation.representations.cdi.SelectByAcceptHeader;
import org.letustakearest.presentation.representations.siren.BookingRepresentationBuilder;
import org.letustakearest.presentation.transitions.CreateBookingAsPlaceTransition;
import org.letustakearest.presentation.transitions.CreateBookingTransition;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

/**
 * @author volodymyr.tsukur
 */
@Path("/bookings")
public class BookingsResource {

    @Inject
    private BookingService bookingService;

    @Inject
    @SelectByAcceptHeader
    private BookingsRepresentationAssembler bookingsRepresentationAssembler;

    @GET
    @Produces({ Siren4J.JSON_MEDIATYPE })
    public Response browse() {
        return Response.
                ok(bookingsRepresentationAssembler.from(bookingService.findAll())).
                cacheControl(CacheControlFactory.oneMinute()).
                build();
    }

    @POST
    @Produces({ Siren4J.JSON_MEDIATYPE })
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(final CreateBookingTransition transition, @Context final UriInfo uriInfo) {
        final Booking result;
        try {
            result = bookingService.create(transition);
        } catch (EntityNotFoundException e) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        final URI bookingURI = BookingRepresentationBuilder.selfURI(result, uriInfo);
        return Response.created(bookingURI).build();
    }

    @POST
    @Produces({ Siren4J.JSON_MEDIATYPE })
    @Consumes("application/vnd.booking.v2+json")
    public Response create(final CreateBookingAsPlaceTransition transition, @Context final UriInfo uriInfo) {
        final Booking result;
        try {
            result = bookingService.create(transition);
        } catch (EntityNotFoundException e) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        final URI bookingURI = BookingRepresentationBuilder.selfURI(result, uriInfo);
        return Response.created(bookingURI).build();
    }

    @Path("/{id}")
    public BookingResource item(@PathParam("id") final Long id) {
        return new BookingResource(id, bookingService);
    }

}
