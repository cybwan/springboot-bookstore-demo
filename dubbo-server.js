pipy.listen(20880, $=>$
  .decodeDubbo()
  .replaceMessage(
    (req) => new Message(
      req.head,
      Hessian.encode([Hessian.decode(req.body)[5]])
    )
  )
  .encodeDubbo()
)