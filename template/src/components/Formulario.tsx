'use client'


import { RegisterFormData, registerSchema } from "../lib/schemas/schemas"
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form"
import { toast } from 'react-toastify';




export default function Formulario(){
    const {
        register,
        handleSubmit,
        formState:{errors,isSubmitting},
        reset,
    }=useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
    defaultValues:{
      idUsuario:1
    }
  });


const onSubmit = async (data: RegisterFormData) => {
  try {
    const API_URL = (import.meta as any).env.VITE_API_URL;

    const response = await fetch(`${API_URL}/registrar-historia-clinica`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    });

    const textoRespuesta = await response.text();

    if (!response.ok) {

  // Limpiamos el texto por si incluye el prefijo '400 BAD_REQUEST'
      const mensajeLimpio = textoRespuesta
        .replace(/^[0-9]{3}\s+[A-Z_]+\s*"?/, '') // Elimina '400 BAD_REQUEST "'
        .replace(/"$/, '');                       // Elimina las comillas del final

      toast.error(mensajeLimpio || 'Error al procesar la solicitud');
      return;
    }

    // 3. Si la respuesta fue exitosa
    toast.success('¡Historia clínica registrada con éxito!');
    console.log('Respuesta del servidor:');
    reset();


    const resultado = await response.json();
    console.log('Usuario registrado:', resultado);
    reset();
  } 
  catch (error) {
    console.error('Error de red o conexión:', error);
  }
};



  return (
    <form onSubmit={handleSubmit(onSubmit)} className="max-w-md space-y-4">

        <div className="flex flex-row gap-x-4">
      <div>
        <label className="block text-sm font-medium">Apellido Paterno</label>
        <input
          {...register('apellidoPaterno',{onChange(event) {
            event.target.value = event.target.value.toUpperCase()
          }})}
          className="w-full rounded border p-2 text-black"
        />
        {errors.apellidoPaterno && (
          <p className="mt-1 text-sm text-red-500">{errors.apellidoPaterno.message}</p>
        )}
      </div>

      <div>
        <label className="block text-sm font-medium">Apellido Materno</label>
        <input
          {...register('apellidoMaterno',{onChange(event){
            event.target.value = event.target.value.toUpperCase()
          }})}
          className="w-full rounded border p-2 text-black"
        />
        {errors.apellidoMaterno && (
          <p className="mt-1 text-sm text-red-500">{errors.apellidoMaterno.message}</p>
        )}
      </div>
        </div>
      <div>
        <label className="block text-sm font-medium">Nombres</label>
        <input
          {...register('nombres',{onChange(event) {
            event.target.value = event.target.value.toUpperCase()
          },})}
          className="w-full rounded border p-2 text-black"
        />
        {errors.nombres && (
          <p className="mt-1 text-sm text-red-500">{errors.nombres.message}</p>
        )}
      </div>

       <div>
        <label className="block text-sm font-medium ">DNI</label>
        <input
          {...register('dni')}
          className="w-full rounded border p-2 text-black"
        />
        {errors.dni && (
          <p className="mt-1 text-sm text-red-500">{errors.dni.message}</p>
        )}
      </div>

      <button
        type="submit"
        disabled={isSubmitting}
        className="w-full rounded bg-blue-600 p-2 text-white hover:bg-blue-700 disabled:opacity-50"
      >
        {isSubmitting ? 'Enviando...' : 'Registrar'}
      </button>
        
    </form>
    
  );

} 