import {number, z} from "zod"

export const registerSchema = z.object({
    idUsuario:number(),
    apellidoPaterno:z.string().nonempty("el apellido paterno es obligatorio"),
    apellidoMaterno:z.string().nonempty("el apellido materno es obligatorio"),
    nombres:z.string().min(2,"el nombre debe tener al menos dos caracteres"),    
    dni:z.string().min(8,"el numero de digitos debe ser mayor a 8")
    .regex(/^\d+$/, "El DNI solo debe contener números"), 
})


export type RegisterFormData = z.infer<typeof registerSchema>