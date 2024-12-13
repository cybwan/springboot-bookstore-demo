pipy.listen(6666, $=>$
  .decodeDubbo()
  .demux().to($=>$
    .mux().to($=>$
      .encodeDubbo()
      .connect('10.0.0.2:6666')
      .decodeDubbo()
    )
  )
  .encodeDubbo()
)